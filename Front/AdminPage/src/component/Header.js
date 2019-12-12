import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Chip from '@material-ui/core/Chip';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import DeleteIcon from '@material-ui/icons/Delete';
import { faPenSquare, faDownload, faUpload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import FileNameChangeModal from './modal/FileNameChangeModal'
import DeleteFileConfirmModal from './modal/DeleteFileConfirmModal'
import FileUploadModal from './modal/FileUploadModal'
import { fileTransferClient2Server } from '../util/APIUtils'
import { observer } from 'mobx-react';
import useStores from '../util/useStore'

const useStyles = makeStyles(theme => ({
  headerroot: {
    width: '100%',
    height: '100%',
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
}));

const TableHeader = observer((props) => {
  const classes = useStyles();
  const [fileNameChangeModalVisible, setFileNameChangeModal] = React.useState(false);
  const [deleteFileComformModalVisible, setDeleteFileComformModal] = React.useState(false);
  const [fileUploadModalVisible, setFileUploadModalVisible] = React.useState(false);
  const { store } = useStores()

  function validation() {
    if (store.selectedPath === '') {
      alert("선택된 파일이 없습니다.")
      return false;
    }
    return true;
  }

  function onClickChangeNameItem(event) {
    if (validation()) {
      setFileNameChangeModal(true);
    }
  }

  function onClickFileDownload(event) {
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

  function onClickFileUpload(event) {
    if (store.currentClientPath === '') {
      alert("선택된 파일이 없습니다.")
      return false;
    }
    setFileUploadModalVisible(true);
  }

  function onClickFileDelete(event) {
    if (validation()) {
      setDeleteFileComformModal(true)
    }
  }

  return (
    <div className={classes.headerroot}>

      <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
      >
        <Grid container item xs={6} spacing={3}>
          <Chip className={classes.path} label={store.currentClientIP + "\\" + store.currentClientPath} />
        </Grid>
        <Grid container item xs={6} spacing={3} justify="flex-end">
          <ButtonGroup variant="text" size="small" aria-label="small contained button group">
            <Button
              variant="contained"
              color="primary" aria-label="CHANGE NAME" component="span" className={classes.iconbutton} onClick={(event) => onClickChangeNameItem(event)} startIcon={
                <FontAwesomeIcon icon={faPenSquare} />}>
              이름 변경
          </Button>

            <Button
              variant="contained"
              color="primary" aria-label="FILE_UPLOAD" component="span" className={classes.iconbutton} onClick={(event) => onClickFileDownload(event)} startIcon={
                <FontAwesomeIcon icon={faDownload} />}>
              다운로드
          </Button>

            <Button
              variant="contained"
              color="primary" aria-label="FILE_DOWNLOAD" component="span" className={classes.iconbutton} onClick={(event) => onClickFileUpload(event)} startIcon={
                <FontAwesomeIcon icon={faUpload} />}>
              업로드
          </Button>

            <Button
              variant="contained"
              color="primary" aria-label="FILE_DELETE" component="span" className={classes.iconbutton} onClick={(event) => onClickFileDelete(event)} startIcon={
                <DeleteIcon />}>
              삭제
          </Button>
          </ButtonGroup>

        </Grid>

      </Grid>
      <FileNameChangeModal open={fileNameChangeModalVisible} setOpen={function () { setFileNameChangeModal(false) }} />
      <DeleteFileConfirmModal open={deleteFileComformModalVisible} setOpen={function () { setDeleteFileComformModal(false) }} />
      <FileUploadModal open={fileUploadModalVisible} setOpen={function () { setFileUploadModalVisible(false) }} />
    </div>
  );
})

export default TableHeader;