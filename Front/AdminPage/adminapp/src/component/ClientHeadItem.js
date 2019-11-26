import React from 'react';
import { observer } from 'mobx-react';
import TreeItem from '@material-ui/lab/TreeItem';
import { faDesktop } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getDirectores } from "../util/APIUtils"
import ClientTreeItem from './ClientTreeItem'


const ClientHeadItem = observer((props) => {
    const { labelText, ...other } = props
    const path = labelText;
    const [directories, setDirectories] = React.useState([]);

    function onClick() {
        getDirectores(path, "root").then(response => {
            console.log(response)
            response["here"] = path
            let tag = document.getElementById(path)
            var array = [];
            response.map((dir, index) => {
                if (dir.i) {
                    array.push(dir.f);
                }
            });
            setDirectories(array);
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
            <span><FontAwesomeIcon icon={faDesktop} size='1x' style={{ marginRight: '7px' }} />{labelText}</span>
        }
            onClick={onClick}
            {...other} >
            {/* <Router> */}
                {directories.map((row, index) => {
                    return (
                        <ClientTreeItem match={{ip:labelText, name:row, absolutePath:row,}} />
                );
                })}
            {/* </Router> */}
        </TreeItem>
    );

});

export default ClientHeadItem;
