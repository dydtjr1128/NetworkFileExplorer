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
    const ip = match.ip//192.168.0.0.1
    const path = match.absolutePath//c://windows/aa
    const name = match.name//aa
    const { store } = useStores()
    //console.log(ip + "=="+path+"=="+name)
    function onClickClient() {
        getDirectores(ip, path).then(response => {
            //console.log(response)
            var array = [];
            response.map((dir, index) => {
                if (dir.i) {
                    array.push(dir.f);
                }
            });
            setDirectories(array);
            store.currentClientIP = ip;
            store.currentClientPath = path;
            store.currentDirectoriesList = response;
        }).catch(error => {
            if (error.status === 401) {
                alert('Not authenticated')
            } else {
                alert(error.message || 'Sorry! Something went wrong. Please try again!')
            }
        });
    }

    return (
        <TreeItem label={
            <span><FontAwesomeIcon icon={faFolder} size='1x' style={{ marginRight: '7px' }} />{name}</span>
        }
            onClick={onClickClient}
            nodeId={path}
            {...match}>

            {directories.map((row, index) => {
                return (
                    <ClientTreeItem match={{ ip: ip, name: row, absolutePath: path + "\\" + row }} />

                );
            })}

        </TreeItem>
    );

});

export default ClientTreeItem;
