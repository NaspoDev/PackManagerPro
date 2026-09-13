![PackManagerPro Banner](https://i.imgur.com/hUROCEv.png)

# Overview
PackManagerPro allows server administrators to manage server resource packs with much more freedom.\
No need to edit the server.properties file or even restart the server. It also supports per-world resource packs.

## Download
[<img src="https://i.imgur.com/SgO3sQT.png" width="180" alt="Available on Modrinth">](https://modrinth.com/plugin/packmanagerpro)
[<img src="https://i.imgur.com/r1ZIvDN.png" width="180" alt="Available on Hangar">](https://hangar.papermc.io/Naspo/PackManagerPro)

Alternatively, you can also download Tether from its [releases on GitHub](https://github.com/NaspoDev/PackManagerPro/releases).

## Compatability
[<img src="https://i.imgur.com/E76tnrm.png" width="180" alt="Available for Paper">](https://papermc.io/)
[<img src="https://i.imgur.com/HkbCj5L.png" width="180" alt="Available for Purpur">](https://purpurmc.org/)

Versions before 2.0.0 also support plain [Spigot](https://www.spigotmc.org/) servers.

# Features
- Manage server resource pack and it's settings without needing to touch the server.properties.
- No need to restart the server to implement changes, just reload the plugin.
- Set per-world resource packs.
- Toggle the server resource pack(s) at any time.

# Commands
- `/pmp reload` - Reloads the configuration.

# Permissions
- `pmp.reload` - Allows /pmp reload.

# Hosting a Resource Pack Guide
In order for this plugin to work, it needs to access the direct download link to a resource pack.\
If you need to create your own direct download link, this guide will show you how to do so with Dropbox.

1. Create/log-into your Dropbox account.
2. Upload the resource pack of choice.
3. Click "share" on the uploaded resource pack.
4. Click "create and copy link".
5. Paste that link into the "pack-url" section of the config.
6. Change "dl=0" to "dl=1" at the end of the link. (This is what makes it a direct download link. It will not work without this).
7. Reload the plugin and you're done!

## Plugin Demo
See PMP in action [here](https://imgur.com/a/packmanagerpro-plugin-demo-yomPYQ2)!