async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (!response.ok) {
    const problem = await response
      .json()
      .catch(() => null)

    throw new Error(
      problem?.detail
        ?? `Request failed with status ${response.status}`,
    )
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

export function createDemoSession(role, userId) {
  return request('/api/demo/session', {
    method: 'POST',
    body: JSON.stringify({
      role,
      userId: Number(userId),
    }),
  })
}

export function getLanguages() {
  return request('/api/reference/languages')
}

export function getSkills() {
  return request('/api/reference/skills')
}

export function createCustomerChat(customerId) {
  return request(`/api/customers/${customerId}/chats`, {
    method: 'POST',
  })
}

export function getChatDetails(chatId) {
  return request(`/api/chats/${chatId}`)
}

export function getRecentMessages(chatId, userId) {
  return request(
    `/api/chats/${chatId}/users/${userId}/messages`,
  )
}

export function getAgentOpenChats(agentId) {
  return request(`/api/agents/${agentId}/chats/open`)
}

export function acceptChat(chatId) {
  return request(`/api/chats/${chatId}/accept`, {
    method: 'PATCH',
  })
}

export function closeChat(chatId) {
  return request(`/api/chats/${chatId}/close`, {
    method: 'PATCH',
  })
}

export function selectChatLanguage(chatId, language) {
  return request(`/api/chats/${chatId}/triage/language`, {
    method: 'PATCH',
    body: JSON.stringify({
      language,
    }),
  })
}

export function selectChatTopic(chatId, skillName) {
  return request(`/api/chats/${chatId}/triage/topic`, {
    method: 'PATCH',
    body: JSON.stringify({
      skillName,
    }),
  })
}
