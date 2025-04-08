package com.example.myapplication

class Welcome {
    sealed class Screen(val route: String) {
        object Welcome : Screen("welcome")
        object MainMenu : Screen("main_menu")
        object StartJournal : Screen("start_journal")
        object DisplayJournals : Screen("display_journals")
    }
}
