// use an integer for version numbers
version = 1

cloudstream {
    description = "This plugin adds all repositories each time the app is opened. " +
            "Uninstall after one run if you do not want all repositories. " +
            "Does not automatically refresh the repository list."

    /**
    * Status int as the following:
    * 0: Down
    * 1: Ok
    * 2: Slow
    * 3: Beta only
    * */
    status = 1
    language = "en"
}
