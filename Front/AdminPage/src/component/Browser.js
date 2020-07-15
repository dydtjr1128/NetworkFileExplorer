import React from 'react';

import Header from './Header';
import Sidebar from './Sidebar';
import DirectoryTable from './DirectoryTable';
import SockJsClient from "react-stomp";
import "./browser_style.scss";
import { ACCESS_TOKEN } from '../util/Constants';
import { getClients } from '../util/APIUtils';
import useStores from '../util/useStore'
import SplitPane from 'react-split-pane';
import Pane from 'react-split-pane/lib/Pane';
import OverlayLoading from './OverlayLoading'

export default function Browser() {
    const [adminRef, setClientRef] = React.useState(null);
    const wsSourceUrl = 'http://localhost:8080/messaging';
    const customHeaders = {
        "authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN),
    };
    const { store } = useStores()

    function sortedIndex(array, value) {
        var low = 0,
            high = array.length;

        while (low < high) {
            var mid = low + high >>> 1;
            if (array[mid] < value) low = mid + 1;
            else high = mid;
        }
        return low;
    }

    function insert(data) {
        store.client_list.splice(sortedIndex(store.client_list, data), 0, data);
    }

    function onMessageReceive(msg, topic) {
        //console.log("Chatting Template - onMessageReceive");      
        console.log("receive message : " + topic + JSON.stringify(msg));
        if (msg.state === 0) {// add
            insert(msg.message);
        } else if (msg.state === 1) { // rmove
            var index = store.client_list.indexOf(msg.message);
            if (index !== -1) {
                if (msg.message === store.currentClientIP) {
                    store.clearClient();
                }
                store.client_list.splice(index, 1);
            }
        } else if (msg.state === 2 || msg.state === 3) { // download success or fail
            alert(msg.message)
        }
    }

    function onConnect() {
        store.progressVisible = true;
        getClients().then(response => {
            store.client_list = store.client_list.concat(response).sort();
            store.progressVisible = false;
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
        document.location.href = "/"
        alert("연결이 끊겼습니다!\n다른 이용자가 로그인 하였거나 서버에 문제가 발생하였습니다.")

    }

    return (
        <div style={{ height: '100%' }}>
            <OverlayLoading/>
            <SockJsClient url={wsSourceUrl} topics={["/topic/admin"]}
                headers={customHeaders}
                subscribeHeaders={customHeaders}
                onMessage={onMessageReceive} ref={(client) => { setClientRef(client) }}
                onConnect={() => { onConnect() }}
                onDisconnect={() => { onDisConnect() }}
                onConnectFailure={() => { onConnectFailure() }}
                debug={false} />

            <SplitPane split="horizontal" maxSize="100%">
                <Pane initialSize="55px" minSize="55px" maxSize="80px">
                    <Header />
                </Pane>
                <Pane>
                    <SplitPane split="vertical" >
                        <Pane initialSize="25%" minSize="10%" maxSize="50%">
                            <Sidebar />
                        </Pane>
                        <Pane initialSize="75%">
                            <DirectoryTable />
                        </Pane>
                    </SplitPane>
                </Pane>
            </SplitPane>
        </div>

    );
}