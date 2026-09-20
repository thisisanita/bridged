import { useEffect, useRef, useState } from 'react'
import {
  acceptChat,
  closeChat,
  getAgentOpenChats,
  getRecentMessages,
} from '../services/api.js'
import { createChatStompClient } from '../services/stompClient.js'
import { mergeMessages } from '../utils/mergeMessages.js'

// Coordinates the agent's REST queue view and one WebSocket connection for
// whichever ACTIVE chat the agent currently has open.
export function useAgentWorkspace(session) {
  const [openChats, setOpenChats] = useState([])
  const [selectedChat, setSelectedChat] = useState(null)
  const [messages, setMessages] = useState([])
  const [draft, setDraft] = useState('')
  const [connectionStatus, setConnectionStatus] = useState('disconnected')
  const [error, setError] = useState('')
  const [isWorking, setIsWorking] = useState(false)
  const stompClientRef = useRef(null)

  // Polling keeps the list aligned with assignments made by the backend. The
  // list length is also the MVP open-chat counter shown in the header.
  useEffect(() => {
    let cancelled = false

    async function refreshChats() {
      try {
        const chats = await getAgentOpenChats(session.agentId)
        if (!cancelled) {
          setOpenChats(chats)
          setError('')
        }
      } catch (requestError) {
        if (!cancelled) setError(requestError.message)
      }
    }

    refreshChats()
    const timer = window.setInterval(refreshChats, 3000)
    return () => {
      cancelled = true
      window.clearInterval(timer)
    }
  }, [session.agentId])

  // Only ACTIVE chats use live messaging. Selecting a different chat cleans
  // up the previous subscription before creating the next one.
  useEffect(() => {
    if (selectedChat?.status !== 'ACTIVE') return undefined

    let cancelled = false
    const stompClient = createChatStompClient({
      chatId: selectedChat.chatId,
      userId: session.userId,
      onMessage: (message) => {
        setMessages((current) => mergeMessages(current, [message]))
      },
      onConnected: async () => {
        try {
          const history = await getRecentMessages(
            selectedChat.chatId,
            session.userId,
          )
          if (!cancelled) {
            setMessages((current) => mergeMessages(current, history))
          }
        } catch (requestError) {
          if (!cancelled) setError(requestError.message)
        }
      },
      onStatusChange: setConnectionStatus,
      onError: setError,
    })

    stompClientRef.current = stompClient
    stompClient.connect()

    return () => {
      cancelled = true
      stompClientRef.current = null
      stompClient.disconnect()
    }
  }, [selectedChat?.chatId, selectedChat?.status, session.userId])

  function selectChat(chat) {
    setMessages([])
    setSelectedChat(chat)
    setError('')
  }

  async function acceptSelectedChat() {
    if (!selectedChat) return
    setIsWorking(true)
    try {
      const accepted = await acceptChat(selectedChat.chatId)
      const activeChat = { ...selectedChat, ...accepted }
      setSelectedChat(activeChat)
      setOpenChats((current) => current.map((chat) =>
        chat.chatId === activeChat.chatId ? activeChat : chat,
      ))
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsWorking(false)
    }
  }

  async function closeSelectedChat() {
    if (!selectedChat) return
    setIsWorking(true)
    try {
      await closeChat(selectedChat.chatId)
      setOpenChats((current) => current.filter(
        (chat) => chat.chatId !== selectedChat.chatId,
      ))
      setSelectedChat(null)
      setMessages([])
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsWorking(false)
    }
  }

  function sendMessage() {
    const content = draft.trim()
    if (!content) return
    try {
      // The confirmed server broadcast adds the bubble after persistence.
      stompClientRef.current?.send(content)
      setDraft('')
    } catch (sendError) {
      setError(sendError.message)
    }
  }

  return {
    openChats, selectedChat, messages, draft, connectionStatus,
    error, isWorking, setDraft, selectChat, acceptSelectedChat,
    closeSelectedChat, sendMessage,
  }
}
