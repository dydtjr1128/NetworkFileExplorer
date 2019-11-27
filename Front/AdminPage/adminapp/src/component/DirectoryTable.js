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
import { observer } from 'mobx-react';
import { faFolder, faFile, faReply } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getDirectores } from "../util/APIUtils"
import './DirectoryTable.scss'
import useStores from '../util/useStore'

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
  { id: 'f', numeric: false, disablePadding: false, label: '이름' },
  { id: 'm', numeric: true, disablePadding: false, label: '수정한 날짜' },
  { id: 't', numeric: false, disablePadding: false, label: '유형' },
  { id: 's', numeric: true, disablePadding: false, label: '크기' },
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

const useStyles = makeStyles(theme => ({
  root: {
    width: '100%',
    height: '100%',
  },
  paperRoot: {
    width: '100%',
    height: '100%',
    overflow: 'scroll',
    // overflow: 'scroll',
    //backgroundColor:'green',
  },
  table: {
    width: '100%',
    height: '100%',
    paddingBottom: '50px'
  },
  tableWrapper: {
    width: '100%',
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


const DirectoryTable = observer((props) => {
  const classes = useStyles();
  const { store } = useStores()
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('name');
  const [selected, setSelected] = React.useState('');
  const handleRequestSort = (event, property) => {
    const isDesc = orderBy === property && order === 'desc';
    setOrder(isDesc ? 'asc' : 'desc');
    setOrderBy(property);
    console.log("sort? " + isDesc + " " + orderBy)
  };

  const handleClick = (event, name) => {
    store.selectedPath = store.currentClientPath + "\\" + name;
    store.selectedIP = store.currentClientIP;
    setSelected(name);
  };

  const moveDirectory = (event, name, type) => {
    if (type === "파일 폴더") {
      getDirectores(store.currentClientIP, store.currentClientPath + "\\" + name).then(response => {
        if (response === null)
          alert("이동 할 수 없는 경로 입니다!")
        else {
          store.currentDirectoriesList = response;
          store.currentClientPath = store.currentClientPath + "\\" + name;
        }
      }).catch(error => {
        if (error.status === 401) {
          alert('Not authenticated')
        } else {
          alert(error.message || 'Sorry! Something went wrong. Please try again!')
        }
      });
    }
  }

  const moveParentDirectory = (event, name) => {
    if (!store.isRoot()) {//루트 전까지만 이동
      getDirectores(store.currentClientIP, store.getParentPath()).then(response => {
        var array = [];
        if (response === null)
          alert("이동 할 수 없는 경로 입니다!")
        else {
          store.currentDirectoriesList = response;
          store.currentClientPath = store.getParentPath();
        }
      }).catch(error => {
        if (error.status === 401) {
          alert('Not authenticated')
        } else {
          alert(error.message || 'Sorry! Something went wrong. Please try again!')
        }
      });
    }
  };

  const isSelected = name => selected === name;

  function SetIcon(props) {
    const string = props.type;
    if (typeof string != 'string') return;
    if (string.includes('폴더'))
      return <FontAwesomeIcon icon={faFolder} size='1x' />;
    else
      return <FontAwesomeIcon icon={faFile} size='1x' />;

  }

  function numValidation(number) {
    return number.toString().padStart(2, '0')
  }

  function formatDate(date) {
    var t = date.getHours();
    return date.getFullYear() + '-' +
      numValidation((date.getMonth() + 1)) + '-' +
      numValidation(date.getDate()) + ' ' +
      (t >= 12 ? '오후 ' : '오전 ') +
      numValidation(t) + ':' +
      numValidation(date.getMinutes());
  }

  function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }

  return (
    <div className={classes.root}>
      <Paper className={classes.paperRoot}>
        <EnhancedTableToolbar />
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
              rowCount={store.currentDirectoriesList.length}
            />
            <TableBody className="disable-select">
              <TableRow
                hover
                onDoubleClick={event => moveParentDirectory(event, '...')}
                tabIndex={-1}
              >
                <TableCell component="th">
                  <span style={{ marginRight: '7px' }} ><FontAwesomeIcon icon={faReply} size='1x' /> </span>
                  ...
                      </TableCell>
                <TableCell>
                </TableCell>
                <TableCell>
                </TableCell>
                <TableCell>
                </TableCell>
              </TableRow>
              {stableSort(store.currentDirectoriesList, getSorting(order, orderBy))
                .map((row, index) => {
                  const isItemSelected = isSelected(row.f);
                  const labelId = `enhanced-table-checkbox-${index}`;

                  return (
                    <TableRow
                      hover
                      onClick={event => handleClick(event, row.f)}
                      onDoubleClick={event => moveDirectory(event, row.f, row.t)}
                      tabIndex={-1}
                      key={row.f}
                      selected={isItemSelected}
                    >
                      <TableCell component="th" id={labelId} scope="row">
                        <span style={{ marginRight: '7px' }} ><SetIcon type={row.t} /></span>
                        {row.f}
                      </TableCell>
                      <TableCell>{formatDate(new Date(row.m))}</TableCell>
                      <TableCell>{row.t}</TableCell>
                      <TableCell align="right">{row.s === 0 ? '' : numberWithCommas(row.s) + 'KB'}</TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </div>
      </Paper>
    </div>
  );
});
export default DirectoryTable;