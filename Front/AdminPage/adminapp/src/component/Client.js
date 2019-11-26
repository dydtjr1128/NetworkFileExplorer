import React from 'react';
import TreeView from '@material-ui/lab/TreeView';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowDropUpIcon from '@material-ui/icons/ArrowRight';
import ClientHeadItem from './ClientHeadItem'

export default function Client(props) {    
    console.log(props.clientIP +"@@@")
    return (
            <TreeView
            defaultCollapseIcon={<ArrowDropDownIcon />}
            defaultExpandIcon={<ArrowDropUpIcon />}
            defaultEndIcon={<ArrowDropDownIcon />}
            >
                <ClientHeadItem nodeId={props.clientIP} labelText={props.clientIP} id={props.clientIP}/>
                
            </TreeView>
    );
}