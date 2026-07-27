package com.gepetto.funhouse.models

import com.funhouse.shared.common.AppData
import com.gepetto.funhouse.Constants
import com.gepetto.funhouse.intentprocessors.FunHouseState

fun defaultMenuOptions() : List<MenuOption> {
    val menuOptions = listOf(
        MenuOption(
            label = "About ${AppData.appName}",
            state = FunHouseState.AboutState
        ),
        MenuOption(
            label = "Privacy Policy",
            state = FunHouseState.WebPageState(url = Constants.PRIVACY_POLICY_URL)
        ),
        //MenuOption(
        //    label = "Settings",
        //    state = GamesState.SettingsState(Settings()),
        //    function = { SettingsContent(Settings(), onClickAction = onClickAction) }),
        //Option(
        //    label = "Exit",
        //    state = GamesState.ExitState
        //),
    )
    return menuOptions
}