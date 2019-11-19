import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { lighten, makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';

import {faFolder, faFile} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import './DirectoryTable.scss'

function createData(name, modifiedDate, type, size) {
  return { name, modifiedDate, type, size };
}

const rows = [
  createData('apple.exe', 305, '응용 프로그램', 6),
  createData('movie.mp4', 452, 'MP4 파일', 51),
  createData('Eclair', 262, '파일 폴더', 24),
  createData('Frozen yoghurt', 159, '파일 폴더', 24),
  createData('movie2.mp4', 356, 'MP4 파일', 49),
  createData('Honeycomb', 408, '파일 폴더', 87),
  createData('Ice cream sandwich', 237, '파일 폴더', 37),
  createData('Jelly Bean', 375, '파일 폴더', 94),
  createData('KitKat', 518, '파일 폴더', 65),
  createData('Lollipop', 392, '파일 폴더', 98),
  createData('Marshmallow', 318,'파일 폴더', 81),
  createData('Nougat', 360, '파일 폴더', 9,),
  createData('Oreo', 437, '파일 폴더', 63),
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
    { id: 'name', numeric: false, disablePadding: false, label: '이름' },
    { id: 'modifiedDate', numeric: true, disablePadding: false, label: '수정한 날짜' },
    { id: 'type', numeric: true, disablePadding: false, label: '유형' },
    { id: 'size', numeric: true, disablePadding: false, label: '크기' },
];

function EnhancedTableHead(props) {
  const { classes, order, orderBy, onRequestSort } = props;
  const createSortHandler = property => event => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>        
        {headCells.map(headCell => (
          <TableCell
            key={headCell.id}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'default'}
            sortDirection={orderBy === headCell.id ? order : false}
          >
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={order}
              onClick={createSortHandler(headCell.id)}
            >
              {headCell.label}
              {orderBy === headCell.id ? (
                <span className={classes.visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </span>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

EnhancedTableHead.propTypes = {
  classes: PropTypes.object.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  order: PropTypes.oneOf(['asc', 'desc']).isRequired,
  orderBy: PropTypes.string.isRequired,
};

const useToolbarStyles = makeStyles(theme => ({
  root: {
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(1),
  },
  highlight:
    theme.palette.type === 'light'
      ? {
          color: theme.palette.secondary.main,
          backgroundColor: lighten(theme.palette.secondary.light, 0.85),
        }
      : {
          color: theme.palette.text.primary,
          backgroundColor: theme.palette.secondary.dark,
        },
  title: {
    flex: '1 1 100%',
  },
}));

const EnhancedTableToolbar = props => {
  const classes = useToolbarStyles();

  return (
    <Toolbar
      className={clsx(classes.root)}
    >
        <Typography className={classes.title} variant="h6" id="tableTitle">
          탐색기
        </Typography>      
    </Toolbar>
  );
};

EnhancedTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired,
};

const useStyles = makeStyles(theme => ({
  root: {
    width: '70%',
  },
  paper: {
    width: '100%',
    height: '95vh',
    backgroundColor:'green',
  },  
  table: {
    minWidth: '100%',
  },
  tableWrapper: {
    width: '100%',
    overflowX: 'auto',
  },
  visuallyHidden: {
    border: 0,
    clip: 'rect(0 0 0 0)',
    height: 1,
    margin: -1,
    overflow: 'hidden',
    padding: 0,
    position: 'absolute',
    top: 20,
    width: 1,
  },
}));


export default function DirectoryTable() {
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

  function SetIcon(props){
    const string = props.type;
    if(typeof string != 'string') return;
    if(string.includes('폴더'))
        return <FontAwesomeIcon icon={faFolder} size='1x'/>;
    else
        return <FontAwesomeIcon icon={faFile} size='1x'/>;
    
  }

  return (
    <div className={classes.root}>      
      <Paper className={classes.paper}>
        <EnhancedTableToolbar numSelected={selected.length} />
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
                      key={row.name}
                      selected={isItemSelected}
                    >                     
                      <TableCell component="th" id={labelId} scope="row">
                        <span style={{marginRight:'7px'}} ><SetIcon type={row.type}/></span>
                        {row.name}
                      </TableCell>
                      <TableCell>{row.modifiedDate}</TableCell>
                      <TableCell>{row.type}</TableCell>
                      <TableCell>{(row.size/1024).toFixed(2)}KB</TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </div>
      </Paper>
    </div>
  );
}