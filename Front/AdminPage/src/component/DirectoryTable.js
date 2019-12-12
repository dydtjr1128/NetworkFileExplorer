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
import { fileTransferClient2Server } from '../util/APIUtils'
import { observer } from 'mobx-react';
import { faFolder, faFile, faReply } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import { getDirectores, copyFile, moveFile } from "../util/APIUtils"

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
  if (array === undefined)
    return [];
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

  const [contextMenu, setContextMenu] = React.useState({
    x: 0,
    y: 0,
    fileName: '',
  });
  const [contextMenuVisible, setContextMenuVisible] = React.useState(false);
  const [contextMenuAction, setContextMenuAction] = React.useState(1);
  const [contextMenuNormalClose, setContextMenuNormalClose] = React.useState(1);

  function onContextMenu(event, fileName, index) {
    event.preventDefault();
    setContextMenu({
      x: event.clientX,
      y: event.clientY,
      fileName: fileName
    })
    setContextMenuVisible(true)
    console.log(contextMenu)
  }
  function onContextMenuFromRoot(event) {
    event.preventDefault();
    if (store.copymoveData.ip !== '') {
      setContextMenu({
        x: event.clientX,
        y: event.clientY,
        fileName: contextMenu.fileName
      })
      setContextMenuVisible(true)
    }
  }

  function handleClose() {
    setContextMenuNormalClose(false)
    setContextMenuVisible(false)
  }

  function changeData() {
    if (contextMenuNormalClose) {
      const fname = contextMenu.fileName;
      if (store.copymoveData.ip === '') {//initial setting
        store.copymoveData.ip = store.currentClientIP;
        store.copymoveData.fileName = fname;
        store.copymoveData.path = store.currentClientPath + "\\" + fname;
        if (contextMenuAction === 1) {// copy          
          store.copymoveData.action = store.copymoveData.action.splice(0, 1);
          store.copymoveData.action[0].label = "현재 위치로 복사하기"

        } else { // move
          store.copymoveData.action = store.copymoveData.action.splice(1, 1);
          store.copymoveData.action[0].label = "현재 위치로 붙여넣기"
        }
        store.copymoveDataJson = store.currentDirectoriesList.find(element => element.f === fname);
        if (store.copymoveDataJson === undefined) {
          alert("오류! 파일이 변경 되었을 수 있습니다.")
          store.clearCopyMoveDatay();
        }
      }
      else {
        if (store.copymoveData.ip !== store.currentClientIP) {
          alert("다른 컴퓨터로 이동/복사 할 수 없습니다.")
          return;
        }
        if (contextMenuAction === 1) {// copy          
          copyFileFromWeb();
        } else { // move
          moveFileFromWeb();
        }
        store.clearCopyMoveDatay();
      }
    }
  }

  function selectHandleClose(e, action) {
    setContextMenuNormalClose(true)
    setContextMenuVisible(false)
    setContextMenuAction(action);
  }

  function copyFileFromWeb() {
    store.progressVisible = true
    copyFile(store.copymoveData.ip, store.copymoveData.path, store.currentClientPath).then(response => {
      // if( store.copymoveData.path === store.currentClientPath){
      //   alert("동일 경로입니다.")
      // } else {
      store.currentDirectoriesList.push(store.copymoveDataJson);
      //}
      alert("복사 성공!")
      //reload
    }).catch(error => {
      if (error.status === 401) {
        alert('Not authenticated')
      } else if (error.status === 400) {
        alert(error.message)
      } else {
        alert(error.message || 'Sorry! Something went wrong. Please try again!')
      }
    }).finally(() => {
      store.progressVisible = false;
    });
  }
  function moveFileFromWeb() {
    store.progressVisible = true
    moveFile(store.copymoveData.ip, store.copymoveData.path, store.currentClientPath).then(response => {

      store.currentDirectoriesList.push(store.copymoveDataJson);
      alert("이동 성공!")
      //reload
    }).catch(error => {
      if (error.status === 401) {
        alert('Not authenticated')
      } else if (error.status === 400) {
        alert(error.message)
      } else {
        alert(error.message || 'Sorry! Something went wrong. Please try again!')
      }
    }).finally(() => {
      store.progressVisible = false;
    });
  }

  const handleRequestSort = (event, property) => {
    const isDesc = orderBy === property && order === 'desc';
    setOrder(isDesc ? 'asc' : 'desc');
    setOrderBy(property);
    console.log("sort? " + isDesc + " " + orderBy)
  };

  const handleClick = (event, row, index) => {
    store.selectedPath = store.currentClientPath + "\\" + row.f;
    store.selectedIP = store.currentClientIP;
    store.selectedType = row.t;
    store.selectedIndex = index;
    setSelected(row.f);
  };

  const moveDirectory = (event, name, type) => {
    if (type === "파일 폴더") {
      store.progressVisible = true;
      getDirectores(store.currentClientIP, store.currentClientPath + "\\" + name).then(response => {
        if (response === null)
          alert("이동 할 수 없는 경로 입니다!")
        else {
          store.clearSelectedData();
          store.currentDirectoriesList = response;
          store.currentClientPath = store.currentClientPath + "\\" + name;
        }
      }).catch(error => {
        if (error.status === 401) {
          alert('Not authenticated')
        } else {
          alert(error.message || 'Sorry! Something went wrong. Please try again!')
        }
      }).finally(() => {
        store.progressVisible = false;
      });
    } else {
      var isFileDownload = window.confirm("해당 파일을 다운로드 하시겠습니까?");
      if (isFileDownload) {
        onClickFileDownload()
      }
    }
  }

  function validation() {
    if (store.selectedPath === '') {
      alert("선택된 파일이 없습니다.")
      return false;
    }
    return true;
  }
  function onClickFileDownload() {
    if (validation()) {
      if (store.selectedType === '파일 폴더') {
        alert("폴더가 아닌 파일을 선택하세요.")
        return;
      }
      console.log(store.selectedIP + " " + store.selectedPath)
      fileTransferClient2Server(store.selectedIP, store.selectedPath).then(response => {
        alert(store.selectedPath + " 다운로드 시작!")
        //reload
      }).catch(error => {
        if (error.status === 401) {
          alert('Not authenticated')
        } else if (error.status === 400) {
          alert(error.message)
        } else {
          alert(error.message || 'Sorry! Something went wrong. Please try again!')
        }
      });
    }
  }

  const moveParentDirectory = (event, name) => {
    if (!store.isRoot()) {//루트 전까지만 이동
      store.progressVisible = true;
      getDirectores(store.currentClientIP, store.getParentPath()).then(response => {
        if (response === null)
          alert("이동 할 수 없는 경로 입니다!")
        else {
          store.clearSelectedData();
          store.currentDirectoriesList = response;
          store.currentClientPath = store.getParentPath();
        }
      }).catch(error => {
        if (error.status === 401) {
          alert('Not authenticated')
        } else {
          alert(error.message || 'Sorry! Something went wrong. Please try again!')
        }
      }).finally(() => {
        store.progressVisible = false;
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
    <div className={classes.root} onContextMenu={event => onContextMenuFromRoot(event)}>
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
              rowCount={store.currentDirectoriesList === [] ? store.currentDirectoriesList.length : 0}
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
                      onClick={event => handleClick(event, row, index)}
                      onContextMenu={event => onContextMenu(event, row.f, index)}
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
                      <TableCell align="right">{row.t === '파일 폴더' ? '' : numberWithCommas(row.s) + 'KB'}</TableCell>
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </div>
      </Paper>
      <Menu
        keepMounted
        transitionDuration={200}
        open={contextMenuVisible}
        onClose={handleClose}
        onExited={changeData}
        anchorReference="anchorPosition"
        anchorPosition={
          { top: contextMenu.y, left: contextMenu.x }
        }
      >
        {store.copymoveData.action.map((el, index) => {
          return (
            <MenuItem onClick={(e) => selectHandleClose(e, el.act)} key={index}>
              <span><FontAwesomeIcon icon={el.icon} />{el.label}</span>
            </MenuItem>
          )
        })}
      </Menu>
    </div>
  );
});
export default DirectoryTable;