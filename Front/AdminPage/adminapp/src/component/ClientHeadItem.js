import React from 'react';
import { observer } from 'mobx-react';
import TreeItem from '@material-ui/lab/TreeItem';
import { faDesktop } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getDirectores } from "../util/APIUtils"
import ClientTreeItem from './ClientTreeItem'
import useStores from '../util/useStore'

const ClientHeadItem = observer((props) => {
    const { path, ...other } = props
    const [directories, setDirectories] = React.useState([]);
    const { store } = useStores();

    function onClick() {
        getDirectores(path, "root").then(response => {
            //response["here"] = path
            // var array = [];
            response.map((dir, index) => {
                store.root_drive.push(dir.f);
            });
            setDirectories(response);
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
            <span className="disable-select"><FontAwesomeIcon icon={faDesktop} size='1x' style={{ marginRight: '7px' }} />{path}</span>
        }
            onClick={onClick}
            {...other} >
            {directories.map((row, index) => {
                return (
                    <ClientTreeItem match={{ ip: path, name: row.f, absolutepath: row.f, nodeId: path + "\\" + row.f }} key={path + "\\" + row.f} />
                );
            })
            }
        </TreeItem>
    );

});

export default ClientHeadItem;
