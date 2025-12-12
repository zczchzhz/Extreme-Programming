/**
 * 导出联系人数据为CSV文件
 */
export const exportToCSV = (contacts, filename = 'contacts.csv') => {
    if (!contacts || contacts.length === 0) {
        alert('没有数据可导出')
        return
    }

    // CSV表头
    const headers = ['姓名', '电话', '邮箱', '微信', 'QQ', '地址', '公司','收藏']
    // CSV数据行
    const csvData = contacts.map(contact => [
        contact.name || '',
        contact.phone || '',
        contact.email || '',
        contact.wechat || '',
        contact.qq || '',
        contact.address || '',
        contact.company || '',
        contact.bookmarked ? '是' : '否'

    ])

    // 构建CSV内容
    let csvContent = '\uFEFF' // BOM，确保Excel正确显示中文
    csvContent += headers.join(',') + '\n'
    csvData.forEach(row => {
        csvContent += row.map(field => `"${field}"`).join(',') + '\n'
    })

    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)

    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'

    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
}

/**
 * 导出为JSON文件
 */
export const exportToJSON = (contacts, filename = 'contacts.json') => {
    if (!contacts || contacts.length === 0) {
        alert('没有数据可导出')
        return
    }

    const dataStr = JSON.stringify(contacts, null, 2)
    const blob = new Blob([dataStr], { type: 'application/json' })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'

    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
}