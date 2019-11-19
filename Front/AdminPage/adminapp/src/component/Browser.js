import React from 'react';

import Header from './Header';
import Sidebar from './Sidebar';
import DirectoryTable from './DirectoryTable';
import "./browser_style.scss";

import Grid from '@material-ui/core/Grid';
export default function Browser() {

    return (
        <Grid container>
            <Grid item xs={12}>
                <Header />
            </Grid>
            <Grid item xs={2}>
                <Sidebar />
            </Grid>
            
            <Grid item xs={10}>
                <DirectoryTable/>                   
            </Grid>
        </Grid>
             
    );
}