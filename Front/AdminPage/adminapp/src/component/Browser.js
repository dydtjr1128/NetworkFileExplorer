import React from 'react';

import Header from './Header';
import Sidebar from './Sidebar';
import DirectoryTable from './DirectoryTable';
import SockJsClient from "react-stomp";
import "./browser_style.scss";
import Grid from '@material-ui/core/Grid';
import { ACCESS_TOKEN } from '../util/Constants';
import { getClients } from '../util/APIUtils';
import useStores from '../util/useStore'
import { useHistory } from "react-router-dom";

export default function Browser() {
    const [adminRef, setClientRef] = React.useState(null);
    const history = useHistory();
    const wsSourceUrl = 'http://localhost:8080/messaging';
    const customHeaders = {
        "authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN),
    };
    const { store } = useStores()

    store.requestFileList = requestFileList;


    function requestFileList(direcotryPath) {

    }


    function onMessageReceive(msg, topic) {
        //console.log("Chatting Template - onMessageReceive");      
        console.log("receive message : " + topic + JSON.stringify(msg));
        if (msg.state === 0) {// add
            store.client_list.push(msg.ip);
        } else if (msg.state === 1) { // rmove
            var index = store.client_list.indexOf(msg.ip);
            if (index !== -1) {
                store.client_list.splice(index, 1);
            }
        }
    }

    const sendMessage = (newMessage) => {
        try {
            adminRef.sendMessage("/app/message", JSON.stringify(newMessage));
            return true;
        } catch (e) {
            return false;
        }
    }

    function onConnect() {
        getClients().then(response => {
            store.client_list = store.client_list.concat(response);
        }).catch(error => {
            if (error.status === 401) {
                alert('Not authenticated')
            } else {
                alert(error.message || 'Sorry! Something went wrong. Please try again!')
            }
        });
        console.log("connect!")
    }

    function onDisConnect() {
        console.log("disconnect!")
    }

    function onConnectFailure() {
        console.log("onConnectFailure!")
        adminRef.disconnect()
        history.push('/')
        alert("연결이 끊겼습니다!\n다른 이용자가 로그인 하였거나 서버에 문제가 발생하였습니다.")

    }
    return (
        <Grid container>
            <SockJsClient url={wsSourceUrl} topics={["/topic/admin"]}
                headers={customHeaders}
                subscribeHeaders={customHeaders}
                onMessage={onMessageReceive} ref={(client) => { setClientRef(client) }}
                onConnect={() => { onConnect() }}
                onDisconnect={() => { onDisConnect() }}
                onConnectFailure={() => { onConnectFailure() }}
                debug={false} />
            <Grid item xs={12}>
                <Header />
            </Grid>
            <Grid item xs={2}>
                <Sidebar />
            </Grid>

            <Grid item xs={10}>
                <DirectoryTable />
            </Grid>
        </Grid>
    );
}