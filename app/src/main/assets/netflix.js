(function() {
    console.log("netflix.js loaded");
    var loginButton = document.getElementsByClassName("login-button")[0];
    var loginForm = document.getElementsByTagName("form")[0];
    var facebookForm = document.getElementsByClassName("facebookForm")[0];
    var usernameTextfield = document.getElementById("id_userLoginId");
    var passwordTextfield = document.getElementById("id_password");
    var episodeContainers = document.getElementsByClassName("episodes");

    if (episodeContainers.length > 0) {
        document.getElementsByClassName("action-button")[0].style.visibility = "hidden";
    }

    if (loginForm) {
    	loginForm.addEventListener("submit", loginSubmitted);
    }

    if (facebookForm) {
        facebookForm.style.visibility = "hidden";
    }

    function loginSubmitted(event) {
        if (isEmailValid(usernameTextfield.value)) {
            window.JSInterface.saveLogin(usernameTextfield.value, passwordTextfield.value);
            return true;
        } else {
            alert("netflix_phone_number_error");
            event.preventDefault();
            return false;
        }
    }

    function isEmailValid(email) {
        if (/[^@]+@[^\.]+\..+/.test(email)) {
            return true;
        } else {
            return false;
        }
    }
})();