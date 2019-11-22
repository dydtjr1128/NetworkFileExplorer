import React from 'react';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';

import { faFolder, faFile } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

var count = 0;
function createData(name) {
  count += 1;
  return { count, name };
}

const rows = [
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
  createData('127.0.0.1'),
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

const headCells = [
  { id: 'id', numeric: true, disablePadding: false, label: 'AI' },
  { id: 'name', numeric: false, disablePadding: false, label: '연결된 PC 목록' },
];


const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    height: '100%',
  },
  pathPaper: {
    width: '100%',
    height: '95vh',
    backgroundColor: 'yellow',
  }
}));

function EnhancedTableHead(props) {
  const { classes, order, orderBy, onRequestSort } = props;
  const createSortHandler = property => event => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>
          <TableCell
            key={headCells[1].id}
            align={'left'}
            padding={headCells[1].disablePadding ? 'none' : 'default'}
            sortDirection={orderBy === headCells[1].id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCells[1].id}
              direction={order}
              onClick={createSortHandler(headCells[1].id)}
            >
              {headCells[1].label}
              {orderBy === headCells[1].id ? (
                <span className={classes.visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </span>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))
      </TableRow>
    </TableHead>
  );
}

export default function Sidebar() {
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
            <div className={classes.tableWrapper}>
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
                  rowCount={rows.length}
                />
                <TableBody>
                  {stableSort(rows, getSorting(order, orderBy))
                    .map((row, index) => {
                      const isItemSelected = isSelected(row.name);
                      const labelId = `enhanced-table-checkbox-${index}`;

                      return (
                        <TableRow
                          hover
                          onClick={event => handleClick(event, row.name)}
                          tabIndex={-1}
                          key={row.id}
                          selected={isItemSelected}
                        >
                          <TableCell component="th" id={labelId} scope="row">
                            <span style={{ marginRight: '7px' }} ><FontAwesomeIcon icon={faFolder} size='1x' /></span>
                            {row.name}
                          </TableCell>
                        </TableRow>
                      );
                    })}
                </TableBody>
              </Table>
        </div>
      </Paper>
    </aside>
  );
}