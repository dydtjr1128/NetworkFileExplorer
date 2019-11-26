import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import { observer } from 'mobx-react';
import useStores from '../util/useStore'

const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    height: '5vh',
    padding : '10px 15px'
  },
  pathPaper: {

    width: '100%',
    height: '5%'
  }
}));

const TableHeader = observer((props) => {
  const classes = useStyles();
  const { store } = useStores()
  return (
    <div className={classes.root}>
        {store.currentClientIP + "/" + store.currentClientPath.replace(/\\/g, "/")}
    </div>
  );
})

export default TableHeader;