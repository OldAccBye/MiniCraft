const modules = require('./modules');

getUUIDFromUsername = async (username) => {
    const response = await modules.fetch(`https://api.mojang.com/users/profiles/minecraft/${username}`);
    if (response.status !== 200) return null;
        
    const data = await response.json();
    if (!data.id) return null;
    
    return await data.id.substr(0,8)+"-"+data.id.substr(8,4)+"-"+data.id.substr(12,4)+"-"+data.id.substr(16,4)+"-"+data.id.substr(20);
}

getNameHistory = async (UUID) => {
    const response = await modules.fetch(`https://api.mojang.com/user/profiles/${UUID}/names`);
    if (response.status !== 200) return null;
        
    const data = await response.json();
    return !!data.error ? null : data;
}

module.exports = { getUUIDFromUsername, getNameHistory };