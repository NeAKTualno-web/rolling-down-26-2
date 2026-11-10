# Rolling Down in the Deep — unofficial port (Minecraft 26.2 / 26.3, Fabric)

> **This is an unofficial port.** The original mod is
> [Rolling Down in the Deep](https://github.com/enjarai/rolling-down-in-the-deep) by **enjarai**
> (MIT license). This repository is not affiliated with the original author.
> The full original git history is kept below the port commits.

A lightweight, fully client-side addon for **Do a Barrel Roll** that brings its free camera
controls underwater: full **pitch, yaw and roll** while swimming.

## Branches

| Branch      | Minecraft |
|-------------|-----------|
| `port`      | 26.2      |
| `port-26.3` | 26.3      |

## What this port adds on top of the original

- Walk on the bottom like in vanilla: touching the ground ends persistent swimming
  (hold sprint to keep swimming along the bottom)
- The addon turns off together with Do a Barrel Roll
- Separate swimming sensitivity: mouse pitch / yaw / roll and turning with A / D
- Swimming speed follows Speed / beacon effects
- Russian translation
- Mixins rewritten for the Minecraft 26.x internals (unobfuscated, Mojang names)

## Requirements

- Minecraft 26.2 or 26.3, Fabric Loader 0.19+
- Fabric API
- [Do a Barrel Roll](https://modrinth.com/mod/do-a-barrel-roll) 3.8.4+
- CICADA
- Optional: YACL and Mod Menu (config screen). Press **O** in game to toggle the addon.

## Building

Requires JDK 25.

```
./gradlew build
```

The jar is written to `build/libs/`. The `tools/verify.sh` script (run by CI) checks that every
mixin injection point exists in the Minecraft jar.

## Disclaimer

The mod significantly changes swimming mechanics (including staying in swim mode without moving)
by carefully desyncing with the server. It works on vanilla and most servers, but some
anti-cheats may flag it. Use at your own risk.

## Bugs and feedback

Please open an issue in this repository's **Issues** tab. Do **not** report problems with this
port to the original author.

## Credits and license

- Original mod: [enjarai](https://github.com/enjarai) — MIT license, see [LICENSE](LICENSE)
- Icon: Mizeno (from the original mod)
- This port was made with the help of an AI assistant (Claude) and tested by hand.

---

## По-русски

**Неофициальный порт** мода [Rolling Down in the Deep](https://github.com/enjarai/rolling-down-in-the-deep)
от enjarai на Minecraft 26.2 / 26.3 (Fabric). Клиентский аддон для Do a Barrel Roll:
свободная камера (тангаж, рыскание, крен) под водой. С автором оригинала порт не связан.
Полная история оригинального репозитория сохранена, коммиты порта идут поверх неё.

Что добавлено: ходьба по дну как в ваниле, отключение вместе с Do a Barrel Roll, отдельная
чувствительность при плавании, учёт эффектов скорости, русский перевод.

Об ошибках пишите во вкладку **Issues** этого репозитория, не автору оригинала.
