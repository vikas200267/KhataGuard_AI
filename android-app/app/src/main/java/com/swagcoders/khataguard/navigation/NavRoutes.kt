package com.swagcoders.khataguard.navigation

object NavRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Home = "home"
    const val CreateCase = "create_case"
    const val StateSelection = "state_selection"
    const val Upload = "upload"
    const val Processing = "processing"
    const val ExtractedDetails = "extracted_details"
    const val Graph = "graph"
    const val Discrepancies = "discrepancies"
    const val Evidence = "evidence"
    const val Report = "report"
    const val History = "history"
    const val Settings = "settings"

    // Dynamic routes helper
    fun upload(caseId: String) = "$Upload/$caseId"
    fun processing(caseId: String) = "$Processing/$caseId"
    fun extractedDetails(caseId: String) = "$ExtractedDetails/$caseId"
    fun graph(caseId: String) = "$Graph/$caseId"
    fun discrepancies(caseId: String) = "$Discrepancies/$caseId"
    fun evidence(caseId: String) = "$Evidence/$caseId"
    fun report(caseId: String) = "$Report/$caseId"
}
