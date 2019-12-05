import React from 'react';
import TreeView from '@material-ui/lab/TreeView';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import ArrowDropUpIcon from '@material-ui/icons/ArrowRight';
import ClientHeadItem from './ClientHeadItem'
import { observer } from 'mobx-react';

const Client = observer((props) => {

    return (
        <TreeView
            defaultCollapseIcon={<ArrowDropDownIcon />}
            defaultExpandIcon={<ArrowDropUpIcon />}
            defaultEndIcon={<div style={{ width: 24 }} />}
        >
            <ClientHeadItem nodeId={props.clientIP} path={props.clientIP} id={props.clientIP} className="disable-select" />

        </TreeView>
    );
});

export default Client;