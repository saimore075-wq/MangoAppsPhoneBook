package com.mangoapps.phonebook.core.navigation

sealed class Screen(val route: String) {
    object Contacts : Screen("contacts")
    object CallLogs : Screen("call_logs")
    object Sms : Screen("sms")
}
