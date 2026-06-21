package com.mangoapps.phonebook.feature.calllogs.domain.model

data class CallLog(
    val id: Long,
    val name: String?,
    val number: String,
    val duration: Long,
    val date: Long,
    val type: CallType,
    val simLabel: String?
)

enum class CallType { INCOMING, OUTGOING, MISSED }
