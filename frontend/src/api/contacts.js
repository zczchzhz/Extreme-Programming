import request from '../utils/request'

export const contactsAPI = {
    // 获取所有联系人
    getAllContacts() {
        return request.get('/api/contacts')
    },

    // 获取收藏的联系人
    getBookmarkedContacts() {
        return request.get('/api/contacts/bookmarked')
    },

    // 根据ID获取联系人
    getContactById(id) {
        return request.get(`/api/contacts/${id}`)
    },

    // 添加联系人
    addContact(contact) {
        return request.post('/api/contacts', contact)
    },

    // 更新联系人
    updateContact(id, contact) {
        return request.put(`/api/contacts/${id}`, contact)
    },

    // 删除联系人
    deleteContact(id) {
        return request.delete(`/api/contacts/${id}`)
    },

    // 搜索联系人
    searchContacts(keyword) {
        return request.get('/api/contacts/search', {
            params: { keyword }
        })
    },

    // 收藏联系人
    bookmarkContact(id) {
        return request.put(`/api/contacts/${id}/bookmark`)
    },

    // 取消收藏联系人
    unbookmarkContact(id) {
        return request.delete(`/api/contacts/${id}/bookmark`)
    },

    // 切换收藏状态
    toggleBookmark(id) {
        return request.patch(`/api/contacts/${id}/bookmark/toggle`)
    }
}
