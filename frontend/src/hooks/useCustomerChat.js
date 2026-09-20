import { useEffect, useRef, useState } from 'react'
import {
  createCustomerChat,
  getChatDetails,
  getLanguages,
  getRecentMessages,
  getSkills,
  selectChatLanguage,
  selectChatTopic,
} from '../services/api.js'
import { createChatStompClient } from '../services/stompClient.js'
import { mergeMessages } from '../utils/mergeMessages.js'

// Owns the customer chat state machine. Triage selections and BOT prompts
// are persisted by the backend and returned as confirmed message records.
export function useCustomerChat(session) {
  const [phase, setPhase] = useState('idle')
  const [chat, setChat] = useState(null)
  const [messages, setMessages] = useState([])
  const [languages, setLanguages] = useState([])
  const [skills, setSkills] = useState([])
  const [selectedLanguage, setSelectedLanguage] = useState('')
  const [selectedSkill, setSelectedSkill] = useState('')
  const [draft, setDraft] = useState('')
  const [connectionStatus, setConnectionStatus] =
    useState('disconnected')
  const [error, setError] = useState('')
  const [isWorking, setIsWorking] = useState(false)

  const stompClientRef = useRef(null)

  // Reference choices come from PostgreSQL through Spring rather than being
  // permanently duplicated as frontend constants.
  useEffect(() => {
    let cancelled = false

    Promise.all([getLanguages(), getSkills()])
      .then(([languageOptions, skillOptions]) => {
        if (!cancelled) {
          setLanguages(languageOptions)
          setSkills(skillOptions)
        }
      })
      .catch((requestError) => {
        if (!cancelled) {
          setError(requestError.message)
        }
      })

    return () => {
      cancelled = true
    }
  }, [])

  // REST polling observes lifecycle changes made by the agent workflow.
  // ASSIGNED remains waiting, ACTIVE opens STOMP, and CLOSED ends messaging.
  useEffect(() => {
    if (!['waiting', 'active'].includes(phase) || !chat?.chatId) {
      return undefined
    }

    let cancelled = false

    async function checkStatus() {
      try {
        const details = await getChatDetails(chat.chatId)

        if (cancelled) {
          return
        }

        setChat(details)

        if (details.status === 'ACTIVE') {
          setPhase('active')
        } else if (details.status === 'CLOSED') {
          setPhase('closed')
        }
      } catch (requestError) {
        if (!cancelled) {
          setError(requestError.message)
        }
      }
    }

    checkStatus()
    const timer = window.setInterval(checkStatus, 2000)

    return () => {
      cancelled = true
      window.clearInterval(timer)
    }
  }, [phase, chat?.chatId])

  // ACTIVE starts one STOMP subscription, then loads REST history. Merging by
  // messageId prevents duplicates if a message arrives through both channels.
  useEffect(() => {
    if (phase !== 'active' || !chat?.chatId) {
      return undefined
    }

    let cancelled = false

    const stompClient = createChatStompClient({
      chatId: chat.chatId,
      userId: session.userId,
      onMessage: (message) => {
        setMessages((current) =>
          mergeMessages(current, [message]),
        )
      },
      onConnected: async () => {
        try {
          const history = await getRecentMessages(
            chat.chatId,
            session.userId,
          )

          if (!cancelled) {
            setMessages((current) =>
              mergeMessages(current, history),
            )
          }
        } catch (requestError) {
          if (!cancelled) {
            setError(requestError.message)
          }
        }
      },
      onStatusChange: setConnectionStatus,
      onError: setError,
    })

    stompClientRef.current = stompClient
    stompClient.connect()

    // Closing the page or leaving ACTIVE deactivates STOMP so the browser does
    // not retain an unused WebSocket connection or duplicate subscription.
    return () => {
      cancelled = true
      stompClientRef.current = null
      stompClient.disconnect()
    }
  }, [phase, chat?.chatId, session.userId])

  async function startChat() {
    setError('')
    setIsWorking(true)

    try {
      const createdChat = await createCustomerChat(
        session.customerId,
      )

      setChat(createdChat)
      setMessages(
        createdChat.initialMessage
          ? [createdChat.initialMessage]
          : [],
      )
      setPhase('language')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsWorking(false)
    }
  }

  async function confirmLanguage() {
    const option = languages.find(
      (language) => language.code === selectedLanguage,
    )

    if (!option || !chat?.chatId) {
      return
    }

    setError('')
    setIsWorking(true)

    try {
      const updatedChat = await selectChatLanguage(
        chat.chatId,
        selectedLanguage,
      )

      setChat((current) => ({
        ...current,
        ...updatedChat,
      }))
      setMessages((current) =>
        mergeMessages(current, updatedChat.messages),
      )
      setPhase('topic')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsWorking(false)
    }
  }

  async function confirmTopic() {
    const option = skills.find(
      (skill) => skill.code === selectedSkill,
    )

    if (!option || !chat?.chatId) {
      return
    }

    setError('')
    setIsWorking(true)

    try {
      const updatedChat = await selectChatTopic(
        chat.chatId,
        selectedSkill,
      )

      setChat((current) => ({
        ...current,
        ...updatedChat,
      }))
      setMessages((current) =>
        mergeMessages(current, updatedChat.messages),
      )
      setPhase('waiting')
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setIsWorking(false)
    }
  }

  function sendMessage() {
    const content = draft.trim()

    if (!content) {
      return
    }

    try {
      // No optimistic bubble is added: the server saves first, broadcasts the
      // confirmed message, and only then does onMessage display it.
      stompClientRef.current?.send(content)
      setDraft('')
      setError('')
    } catch (sendError) {
      setError(sendError.message)
    }
  }

  return {
    phase,
    chat,
    messages,
    languages,
    skills,
    selectedLanguage,
    selectedSkill,
    draft,
    connectionStatus,
    error,
    isWorking,
    setSelectedLanguage,
    setSelectedSkill,
    setDraft,
    startChat,
    confirmLanguage,
    confirmTopic,
    sendMessage,
  }
}
