package dev.krysztal.rwm.foundation.infix

import net.kyori.adventure.text.Component

infix fun Component.add(component: Component): Component =
    this.append(component)

infix fun Component.add(any: Any): Component =
    this.append { Component.text(any.toString()) }
