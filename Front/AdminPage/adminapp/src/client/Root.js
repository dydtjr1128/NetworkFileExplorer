import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import App from 'shared/App';
import { Provider } from 'mobx-react'; 
import  ClientStore  from './ClientStore'

const Root = () => (
    <BrowserRouter>
        <Provider store = {ClientStore}>
            <App/>
        </Provider>
    </BrowserRouter>    
);

export default Root;