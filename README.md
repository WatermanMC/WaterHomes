# WaterHomes

**WaterHomes** is a lightweight and efficient home-setting plugin designed for **PaperMC 1.21+** servers. It provides players with the fundamental ability to set personal teleport locations, making world navigation quick and easy without the unnecessary clutter or overhead of larger plugins.

---

## Key Features & Why You Should Download It

* **Essential Home Functionality:** Quickly set your personal spawn location anywhere in the world. No complex features—just simple, reliable `/home` functionality.
* **Minimal Overhead:** Built to be lightweight and efficient, ensuring minimal impact on your server's performance. Perfect for small to medium-sized survival, semi-vanilla, or even large public servers that just need a core home feature.
* **Extra Features:** Extra features like to ban a word if a player is setting a home or unsafe blocks so players cannot set a home near a blacklisted block! View it at your `config.yml`!
* **Customize Plugin Messages:** Customize all WaterHomes messages completely on `messages.yml`! Uses MiniMessage so you can have gradients, hex, clickable text and more! No more ugly § color codes! MiniMessage formatter: [https://webui.advntr.dev/](https://webui.advntr.dev/)
* **Intuitive Commands:** Everything a player needs is covered by a small, easy-to-remember commands set.
* **Modern Server Support:** Built specifically for the latest **PaperMC 1.21+** versions and its forks, guaranteeing up-to-date performance and stability.
* **Open Source:** Developed under the **GNU General Public License v3.0**, allowing for transparency and community contributions.

---

## Commands & Usage
**(<> is optional, [] is required):**

| Command                     | Description                               | Permission            |
|:----------------------------|:------------------------------------------|:----------------------|
| `/sethome <name>`           | Sets a new home at your current location. | `waterhomes.sethome`  |
| `/home <name>`              | Teleports you to your chosen home.        | `waterhomes.home`     |
| `/delhome <name>`           | Delete your home                          | `waterhomes.delhome`  |
| `/homelist`                 | Lists all homes you have set.             | `waterhomes.homelist` |
| `/waterhomes [info/reload]` | Reload or see the info of the plugin.     | `waterhomes.admin`    |

---

## Default Configs
Default configs used on the plugin.

<details>

<summary>config.yml</summary>

```yaml
# Thank you for using WaterHomes!
# Documentation: https://github.com/WatermanMC/WaterHomes/wiki
# Discord: https://discord.gg/Scgqfm5EU4
# GitHub: https://github.com/WatermanMC/WaterHomes

# Teleport Delay
# Bypass permission: waterhomes.tpdelay.bypass
tp-delay:
  enabled: true
  cancel-on-move: true
  duration: 2 # In seconds


# Teleport Cooldown
# Bypass permission: waterhomes.cooldown.bypass
tp-cooldown:
  enabled: true
  duration: 60 # In seconds


# Banned words for sethome
# Regex pattern is allowed
# Bypass permission: waterhomes.bannedwords.bypass
banned-words:
  - banned words is listed here
  - th(i|1)s is (a|4) reg(3|3)x pattern


# Here you can customize for home limits
# Permission: waterhomes.sethome.<rank>
# NOTE: This is not assigned in your permission manager automatically. Look at the example below.
# For example, you set default to create only 1 home. You need to give a group/user the permission: waterhomes.sethome.default
home-limits:
  default: 1
  god: 10


# List unsafe blocks here so players cannot set home at this blocks
# Use PaperMC API Material items (If you dont understand, you can Google :D)
# Bypass permission: waterhomes.unsafeblocks.bypass
# This detects within 2 block radius of player
unsafe-blocks:
  - WATER
  - LAVA
  - CACTUS
  - MAGMA_BLOCK
  - BEDROCK
```
</details>

<details>

<summary>messages.yml</summary>

```yaml
# Customize messages here!
# Only supports MiniMessage!
# MniMessage formatter:

prefix: "<aqua><b>WaterHomes<white>: <reset>"

reloaded: "<aqua><b>WaterHomes <reset><gray>Reloaded!"
nopermission: "<red>You don't have permission to use this command!"

sethome:
  success: "<green>Successfully set home at your location!"
  failed:
    unsafe: "<red>Error! Unsafe location!"
    banword: "<red>Error! Your set home contains a banned word!"
    args: "<red>Error! Invalid Argument!"
    # This is only activated if a player have a permission that grants him a multiple homes
    noname: "<red>Error! No name specified!"

home:
  success: "<green>Teleported to your home!"
  delay: "<green>Teleporting... Please wait."
  cooldown: "<red>You are in cooldown! Please wait in <yellow>%seconds% <red>seconds!"
  failed:
    delaymoved: "<red>You moved! Home teleportation cancelled."
    invalidhome: "<red>Error! Invalid home."
    args: "<red>Error! Invalid Argument!"

delhome:
  success: "<green>Home '<yellow>%home%<green>' has been deleted!"
  failed:
    fail: "<red>Failed to delete %home%."
    nohomes: "<red>You don't have any homes to delete!"
    notfound: "<red>Home '<yellow>%home%<red>' not found!"
    noname: "<red>Please specify a home name to delete!"

homelist:
  - "<gray><b>----- <gold>Home List <gray>-----"
    # This thing repeats, if you have many homes
    # If a player don't have a permission that gives many homes, the home name is "HOME"
  - "<yellow>%home%"
  ```
</details>

---

## Links & Additional Information

**Author:** WatermanMC & Runkang10

* **GitHub Repository** (Source Code & Full Documentation): [https://github.com/WatermanMC/WaterHomes](https://github.com/WatermanMC/WaterHomes)
* **Documentation:** [https://github.com/WatermanMC/WaterHomes/wiki](https://github.com/WatermanMC/WaterHomes/wiki)
* **Discord Support:** [https://discord.gg/Scgqfm5EU4](https://discord.gg/Scgqfm5EU4)

---

This plugin is also owned by **VOXELWARE STUDIOS**.

---