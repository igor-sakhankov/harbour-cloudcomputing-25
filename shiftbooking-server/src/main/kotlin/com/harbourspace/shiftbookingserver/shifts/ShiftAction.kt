package com.harbourspace.shiftbookingserver.shifts

import com.fasterxml.jackson.annotation.JsonCreator

enum class ShiftAction {
    ADD,
    DELETE,
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromValue(value: String): ShiftAction {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid ShiftAction: $value")
        }
    }
}