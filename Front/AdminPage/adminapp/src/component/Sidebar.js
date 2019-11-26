import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import useStores from '../util/useStore'
import { observer } from 'mobx-react';
import Client from './Client'

function desc(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function stableSort(array, cmp) {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = cmp(a[0], b[0]);
    if (order !== 0) return order;
    return a[1] - b[1];
  });
  return stabilizedThis.map(el => el[0]);
}

function getSorting(order, orderBy) {
  return order === 'desc' ? (a, b) => desc(a, b, orderBy) : (a, b) => -desc(a, b, orderBy);
}

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
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('name');
  const [selected, setSelected] = React.useState([]);

  const isSelected = name => selected.indexOf(name) !== -1;

  return (
    <aside className={classes.root}>
      <Paper className={classes.pathPaper}>
        <div className={classes.container}>
          <div>
            연결된 PC 리스트
          </div>
          <div className={classes.clientsContainer}>
            {stableSort(store.client_list, getSorting(order, orderBy))
              .map((row, index) => {
                const isItemSelected = isSelected(row);
                const labelId = `enhanced-table-checkbox-${index}`;

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