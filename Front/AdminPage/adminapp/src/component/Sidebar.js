import React from 'react';
import Paper from '@material-ui/core/Paper';
import { lighten, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles(theme => ({
    root: {                
        width: '100%',
        height: '100%',
    },
    pathPaper: {
      width: '100%',
      height : '95vh',
      backgroundColor: 'yellow',
    }
  }));

export default function TableHeader() {
  const classes = useStyles();
  return (
    <aside className={classes.root}>
        <Paper className={classes.pathPaper}>
            aaa
        </Paper>
    </aside>
  );
}