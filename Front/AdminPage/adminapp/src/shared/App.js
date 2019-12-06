import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { SignIn, Explorer, NotFound } from 'pages';
import { ACCESS_TOKEN } from '../util/Constants';
import { login } from '../util/APIUtils';
import { useHistory } from "react-router-dom";

export default function App() {
    const history = useHistory();
    const [isAuthenticated, setAuthenticated] = React.useState(false);
    //const [access_token, setAccessToken] = React.useState();

    function handleLogin(userID, userPassword) {

        login(userID, userPassword)
            .then(response => {
                console.log("login")
                localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                setAuthenticated(true);
                history.push('/explorer')
            }).catch(error => {
                if (error.status === 401) {
                    alert('Your Username or Password is incorrect. Please try again!')
                } else {
                    alert(error.message || 'Sorry! Something went wrong. Please try again!')
                }
            });

    }

    function handleLogout(redirectTo = "/", description = "You're successfully logged out.") {
        localStorage.removeItem(ACCESS_TOKEN);
        setAuthenticated(false);
    }

    return (
        <Switch>
            <Route exact={true} path="/" render={(props) => <SignIn handleLogin={handleLogin} {...props} />} />
            <Route path="/explorer" render={(props) => <Explorer authenticated={isAuthenticated} handleLogout={handleLogout} {...props} />} />
            <Route component={NotFound} />
        </Switch>
    );
}
