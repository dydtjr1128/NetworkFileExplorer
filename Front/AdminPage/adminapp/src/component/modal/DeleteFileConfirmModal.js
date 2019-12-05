import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import {
    Modal,
    Grid,
    Container,
    Button
} from '@material-ui/core';
import useStores from '../../util/useStore'
import { deleteFile } from "../../util/APIUtils"

const useStyles = makeStyles(theme => ({
    paper: {
        position: 'absolute',
        width: 500,
        height: 250,
        backgroundColor: theme.palette.background.paper,
        padding: theme.spacing(2, 2, 3),
        top: '5%',
        bottom: '60%',
        left: '50%',
        transform: 'translate(-50%)',
        overflow: 'auto',
        fontFamily: 'NanumBarunGothic',
    },
    title: {
        padding: 0,
    },
    gridTitle: {
        marginTop: 20,
    },
    inputTitle: {
        padding: 5,
        marginBlockStart: 0,
        marginBlockEnd: 0,
    },
    inputText: {
        margin: "20px 0px"
    },
    modalBtn: {
        marginRight: "20px"
    }
}));

export default function DeleteFileConfirmModal(props) {
    const classes = useStyles();
    const { store } = useStores()
    const [name, setName] = React.useState();
    function handleChange(event) {
        setName(event.target.value);
    };
    function requestChangeFileName(event) {
        deleteFile(store.selectedIP, store.selectedPath).then(response => {
            alert("삭제 성공!")            
            if (store.currentDirectoriesList.length >= store.selectedIndex) {
                store.currentDirectoriesList.splice(store.selectedIndex, 1);
            }
            store.clearSelectedData();
        }).catch(error => {
            if (error.status === 401) {
                alert('Not authenticated')
            } else if (error.status === 400) {
                alert(error.message)
            } else {
                alert(error.message || 'Sorry! Something went wrong. Please try again!')
            }
        });
        props.setOpen(false);
    }

    return (
        <Modal disablePortal
            disableEnforceFocus
            disableAutoFocus
            open={props.open}>
            <div className={classes.paper}>
                <Container maxWidth="lg">
                    <h2>해당 파일을 지우 시겠습니까?</h2>
                    <hr />
                    <Grid container>
                        <Grid item xs={12} className={classes.inputText}>
                            {store.selectedPath}
                        </Grid>
                        <Grid
                            container
                            direction="row"
                            justify="center"
                            alignItems="center"
                        >
                            <Grid container item xs={12} className={classes.inputText} justify="flex-end">
                                <Button variant="contained" color="primary" className={classes.modalBtn} onClick={(event) => { props.setOpen(false); }}>
                                    취소
                                </Button>
                                <Button variant="contained" color="primary" onClick={(event) => requestChangeFileName(event)}>
                                    확인
                                </Button>
                            </Grid>
                        </Grid>
                    </Grid>
                </Container>
            </div>
        </Modal>
    )
}