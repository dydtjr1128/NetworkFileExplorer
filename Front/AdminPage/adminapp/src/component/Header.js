import React from 'react';
import { lighten, makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';

const useStyles = makeStyles(theme => ({
    root: {
        width: '100%',
        height:'5vh',
        backgroundColor:'blue',
    },
    pathPaper: {
      
      width: '100%',
      height : '5%'
    }
  }));

export default function TableHeader() {
  const classes = useStyles();
  return (
    <div className={classes.root}>
        <Paper className={classes.pathPaper}>
            sdsfd
        </Paper>
    </div>
  );
}