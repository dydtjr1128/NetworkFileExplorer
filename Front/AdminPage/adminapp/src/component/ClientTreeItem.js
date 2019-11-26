import React from 'react';
import { observer } from 'mobx-react';
import TreeItem from '@material-ui/lab/TreeItem';
import { faFolder } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getDirectores } from "../util/APIUtils"
import useStores from '../util/useStore'

const ClientTreeItem = observer((props) => {
    const { match } = props;
    const [directories, setDirectories] = React.useState([]);
    const [response, setResponse] = React.useState([]);
    const ip = match.ip//192.168.0.0.1
    const path = match.absolutepath//c://windows/aa
    const name = match.name//aa
    const { store } = useStores()

    function onClickClient(e) {
        console.log(match.nodeId + "@@@" + ip + "@" + path + "@" + name);
        if (response.length > 0) {
            store.currentClientIP = ip;
            store.currentClientPath = path;
            store.currentDirectoriesList = response;
            //store.addExpanded(match.nodeId)       
            //console.log("로컬!")
        }
        else {
            //console.log("request!")
            getDirectores(ip, path).then(response => {
                var array = [];
                if (response === null)
                    alert("빈 폴더 입니다.!")
                else {
                    var array = []
                    response.map((dir, index) => {
                        if (dir.i) {
                            array.push(dir.f);
                        }
                    });
                    setResponse(response)
                    setDirectories(array);
                    store.currentClientIP = ip;
                    store.currentClientPath = path;
                    store.currentDirectoriesList = response;
                    //store.addExpanded(match.nodeId)
                }
            }).catch(error => {
                if (error.status === 401) {
                    alert('Not authenticated')
                } else {
                    alert(error.message || 'Sorry! Something went wrong. Please try again!')
                }
            });
            console.log(store.expanded)
        }
    }

    return (
        <TreeItem label={
            <span><FontAwesomeIcon icon={faFolder} size='1x' style={{ marginRight: '7px' }} />{name}</span>
        }
            onClick={onClickClient}
            nodeId={path}
            {...match}>

            {directories == null ? alert("읽을 수 없는 폴더 입니다.") :
                directories.map((row, index) => {
                    return (
                        <ClientTreeItem match={{ ip: ip, name: row, absolutepath: path + "\\" + row, nodeId: ip + "\\" + path + "\\" + row }} key={path + "\\" + row} />

                    );
                })}

        </TreeItem>
    );

});

export default ClientTreeItem;
