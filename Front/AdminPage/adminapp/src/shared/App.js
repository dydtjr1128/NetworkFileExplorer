import React, { Component } from 'react';
import { Route } from 'react-router-dom';
import { SignIn, Explorer } from 'pages';

class App extends Component {
    render() {
        return (
            <div>                
                <Route exact path="/" component={SignIn}/>
                <Route path="/explorer" component={Explorer}/>
            </div>
        );
    }
}

export default App;