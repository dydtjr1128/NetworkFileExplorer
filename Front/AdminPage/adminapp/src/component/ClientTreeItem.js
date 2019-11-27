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
    const [responsedData, setResponsedData] = React.useState([]);//지워볼것?
    const ip = match.ip;//192.168.0.0.1
    const path = match.absolutepath;//c://windows/aa
    const name = match.name;//aa
    const { store } = useStores();

    function setGlobalData(res) {
        store.currentClientIP = ip;
        store.currentClientPath = path;
        store.currentClientParentPath = (path === name ? name : path.substring(0, path.lastIndexOf("\\")));
        store.currentDirectoriesList = res;
    }

    function onClickClient(e) {
        //console.log(match.nodeId + "@@@" + ip + "@" + path + "@" + name);
        // console.log("=========")
        // console.log(e.target.parentElement.parentElement)
        if (responsedData.length > 0) {
            setGlobalData(responsedData);
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
                    setGlobalData(response);
                    // response.map((dir, index) => {
                    //     if (dir.i) {
                    //         directories.push(dir.f);
                    //     }
                    // });
                    setResponsedData(response)
                    //setDirectories(array);
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
            <span className="disable-select"><FontAwesomeIcon icon={faFolder} size='1x' style={{ marginRight: '7px' }} />{name}</span>
        }
            onClick={onClickClient}
            {...match}>

            {responsedData == null ? alert("읽을 수 없는 폴더 입니다.") :
                responsedData.filter((row) => {
                    return row.i;
                }).map((row, index) => {
                    var apath = path + "\\" + row.f;                    
                    return (

                        <ClientTreeItem match={{ ip: ip, name: row.f, absolutepath: apath, nodeId: ip + "\\" + apath }} key={apath} />

                    );

                })}

        </TreeItem>
    );

});

export default ClientTreeItem;
