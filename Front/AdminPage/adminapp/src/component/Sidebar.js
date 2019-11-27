import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import useStores from '../util/useStore'
import { observer } from 'mobx-react';
import Client from './Client'

const useStyles = makeStyles(theme => ({
  root: {
     width: '100%',    
     height: '100%',
  },
  pathPaper: {
    width: '100%',    
     height: '100%',
    // height: '100%',
    // width : '100%'
    // overflowX: 'scroll',
    // overflowY: 'scroll'
  },
  container: {
    height : '100%',
    padding: '15px',
    paddingBottom : '100px',
  },
  clientsContainer: {
    height : '95%',
    overflow: 'scroll',
    
  }
}));

const Sidebar = observer((props) => {
  const { store } = useStores()
  const classes = useStyles();

  return (
    <aside className={classes.root}>
      <Paper className={classes.pathPaper}>
        <div className={classes.container}>
          <div>
            연결된 PC 리스트
          </div>
          <div className={classes.clientsContainer}>
            {store.client_list
              .map((row, index) => {

                return (
                  <Client key ={row + index} clientIP={row} />
                );
              })}
          </div>
        </div>
      </Paper>
    </aside>
  );
});

export default Sidebar;