---
hide:
  - navigation
---

# Incompatibilities

This page describes incompatibilities with other mods.

## NeoForge (with Sinytra Connector)

### Problem

As Solstice is developed for the Fabric loader, some functionalities may break on NeoForge.
One of the most notable issues is that command spy does not work properly.

### Solution

Disable command spy. Many attempts have been made to fix this issue, but none have been successful for both loaders.

## [Text Placeholder API Expressions](https://modrinth.com/mod/text-placeholder-api-expr)

### 1.1.0 for Minecraft 1.21.1

#### Problem

This mod includes a downgraded alpha version of Text Placeholder API that Fabric prefers to use and force on other mods
such as Solstice. This downgraded version has a broken parser that causes buttons to lose the click event, thus making
them unusable.

#### Solution

Currently, there is no other solution than to disable this mod.

## [Get Off My Lawn ReServed (GOML) on NeoForge](https://modrinth.com/mod/goml-reserved)

### Problem

Solstice crashes when this mod is present. The issue is caused by a dependency of both mods sgui.
It may be an issue with Sinytra Connector (to test).

### Solution

Currently, there is no other solution than to disable this mod (or Solstice).