import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';
import useStores from '../util/useStore'
import { observer } from 'mobx-react';
import Client from './Client'

var id_ai = 0;
function createData(name) {
  id_ai += 1;
  return { id_ai, name };
}

const rows = [

];

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

const headCells = {
  //{ id: 'id', numeric: true, disablePadding: false, label: 'AI' },
   id: 'name', numeric: false, disablePadding: false, label: '연결된 PC 목록' ,
};


const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    height: '100%',
    overflowX: 'scroll',
    overflowY: 'scroll'
  },
  pathPaper: {
    width: '100%',
    height: '95vh',
    backgroundColor: 'yellow',
  }
}));

function EnhancedTableHead(props) {
  const { classes, order, orderBy, onRequestSort, rowCount } = props;
  const createSortHandler = property => event => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>
          <TableCell
            key={headCells.id}
            align={'left'}
            padding={headCells.disablePadding ? 'none' : 'default'}
            sortDirection={orderBy === headCells.id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCells.id}
              direction={order}
              onClick={createSortHandler(headCells.id)}
            >
              {headCells.label}
            </TableSortLabel>
          </TableCell>
      </TableRow>
    </TableHead>
  );
}


const Sidebar = observer((props) => {
  const { store } = useStores()
  const classes = useStyles();
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('name');
  const [selected, setSelected] = React.useState([]);

  const handleRequestSort = (event, property) => {
    const isDesc = orderBy === property && order === 'desc';
    setOrder(isDesc ? 'asc' : 'desc');
    setOrderBy(property);
  };

  const handleClick = (event, name) => {
    const selectedIndex = selected.indexOf(name);

    console.log(selectedIndex + " " + name)
    setSelected(name);
  };

  const isSelected = name => selected.indexOf(name) !== -1;

  return (
    <aside className={classes.root}>
      <Paper className={classes.pathPaper}>
            {/* <div className={classes.tableWrapper}>
              <Table
                stickyHeader
                className={classes.table}
                aria-labelledby="tableTitle"
                size={'small'}
                aria-label="enhanced table"
              >
                <EnhancedTableHead
                  classes={classes}
                  order={order}
                  orderBy={orderBy}
                  onRequestSort={handleRequestSort}
                  rowCount={store.client_list.length}
                />
                <TableBody> */}
                  {stableSort(store.client_list, getSorting(order, orderBy))
                    .map((row, index) => {
                      const isItemSelected = isSelected(row);
                      const labelId = `enhanced-table-checkbox-${index}`;                      
                      
                      return (                                                 
                            <Client clientIP ={row}/>
                      );
                    })}
                {/* </TableBody>
              </Table>
        </div> */}
      </Paper>
    </aside>
  );
});

export default Sidebar;