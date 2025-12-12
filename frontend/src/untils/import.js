/**
 * 数据导入工具函数
 */

/**
 * 解析CSV文件内容
 */
export const parseCSV = (csvText) => {
    const lines = csvText.split('\n').filter(line => line.trim())
    if (lines.length === 0) return []

    // 解析表头
    const headers = lines[0].split(',').map(header =>
        header.replace(/^"|"$/g, '').trim()
    )

    // 解析数据行
    const contacts = []
    for (let i = 1; i < lines.length; i++) {
        const line = lines[i]
        if (!line.trim()) continue

        // 简单的CSV解析，处理引号内的逗号
        const values = []
        let inQuotes = false
        let currentValue = ''

        for (let j = 0; j < line.length; j++) {
            const char = line[j]

            if (char === '"') {
                inQuotes = !inQuotes
            } else if (char === ',' && !inQuotes) {
                values.push(currentValue.trim())
                currentValue = ''
            } else {
                currentValue += char
            }
        }
        values.push(currentValue.trim())

        // 构建联系人对象
        const contact = {}
        headers.forEach((header, index) => {
            let value = values[index] || ''
            // 移除值中的引号
            value = value.replace(/^"|"$/g, '')

            switch (header) {
                case '姓名':
                    contact.name = value
                    break
                case '电话':
                    contact.phone = value
                    break
                case '邮箱':
                    contact.email = value || null
                    break
                case '微信':  // 新增
                    contact.wechat = value || null
                    break
                case 'QQ':    // 新增
                    contact.qq = value || null
                    break
                case '地址':
                    contact.address = value || null
                    break
                case '公司':
                    contact.company = value || null
                    break
            }
        })

        // 只有姓名和电话都有的联系人才会被导入
        if (contact.name && contact.phone) {
            contacts.push(contact)
        }
    }

    return contacts
}

/**
 * 解析JSON文件内容
 */
export const parseJSON = (jsonText) => {
    try {
        const data = JSON.parse(jsonText)

        // 处理数组格式
        if (Array.isArray(data)) {
            return data.map(item => ({
                name: item.name || '',
                phone: item.phone || '',
                email: item.email || null,
                wechat: item.wechat || null,
                qq: item.qq || null,
                address: item.address || null,
                company: item.company || null,
                avatar: item.avatar || null
            })).filter(contact => contact.name && contact.phone)
        }

        return []
    } catch (error) {
        throw new Error('JSON格式不正确')
    }
}

/**
 * 验证联系人数据
 */
export const validateContacts = (contacts) => {
    const errors = []

    contacts.forEach((contact, index) => {
        if (!contact.name || !contact.name.trim()) {
            errors.push(`第 ${index + 1} 行：姓名为空`)
        }
        if (!contact.phone || !contact.phone.trim()) {
            errors.push(`第 ${index + 1} 行：电话为空`)
        }
        // 简单的电话格式验证
        if (contact.phone && !/^[\d\-+()\s]{6,20}$/.test(contact.phone)) {
            errors.push(`第 ${index + 1} 行：电话格式不正确`)
        }
    })

    return errors
}