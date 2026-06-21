package com.mangoapps.phonebook.feature.sms.domain.model

data class SmsMessage(
    val id: Long,
    val sender: String,
    val body: String,
    val date: Long
)
