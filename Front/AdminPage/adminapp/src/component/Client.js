import React from 'react';
import TreeView from '@material-ui/lab/TreeView';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowDropUpIcon from '@material-ui/icons/ArrowRight';
import ClientHeadItem from './ClientHeadItem'
import { observer } from 'mobx-react';
import useStores from '../util/useStore'
import { toJS } from 'mobx';

const Client = observer((props) => {
    //const { store } = useStores()
    function onNodeToggle(event, nodeId) {
        //console.log("==================")
        //console.log(nodeId)
        //console.log(event.target)
        //store.removeExpanded(nodeId)        
        //store.expanded = nodeId;
        // console.log(event.target)
        // console.log(event)


    }
    return (
        <TreeView
            onNodeToggle={(event, nodeId) => onNodeToggle(event, nodeId)}
            //expanded={toJS(store.expanded)}
            defaultCollapseIcon={<ArrowDropDownIcon />}
            defaultExpandIcon={<ArrowDropUpIcon />}
            defaultEndIcon={<div style={{ width: 24 }} />}
        // defaultEndIcon={<ArrowDropDownIcon />}
        >
            <ClientHeadItem nodeId={props.clientIP} labelText={props.clientIP} id={props.clientIP} className="disable-select" />

        </TreeView>
    );
});

export default Client;