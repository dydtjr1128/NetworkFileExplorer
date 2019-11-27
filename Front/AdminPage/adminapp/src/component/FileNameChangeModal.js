import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import {
    Modal,
    Grid,
    TextField,
    Container,
    FormGroup,
    Button
} from '@material-ui/core';
import useStores from '../util/useStore'
import { changeFileName } from "../util/APIUtils"

const useStyles = makeStyles(theme => ({
    paper: {
        position: 'absolute',
        width: 500,
        height: 350,
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

export default function CustomSetupRequestModal(props) {
    const classes = useStyles();
    const { store } = useStores()
    const [name, setName] = React.useState();
    function handleChange(event) {
        setName(event.target.value);
    };
    function requestChangeFileName(event) {
        alert("request!")
        changeFileName(store.selectedIP, store.selectedPath, name)
        props.setOpen(false);
    }
    return (
        <Modal disablePortal
            disableEnforceFocus
            disableAutoFocus
            open={props.open}>
            <div className={classes.paper}>
                <Container maxWidth="lg">
                    <h2>파일 명 변경</h2>
                    <hr />
                    <Grid container>
                        <Grid item xs={12} className={classes.inputText}>
                            {store.selectedPath}
                        </Grid>
                        <Grid item xs={12} className={classes.inputText}>
                            <TextField
                                required
                                id="standard-full-width"
                                label="변경할 파일명(파일명과 확장자만(파일인 경우) 쓰세요)"
                                placeholder="Windows.h"
                                onChange={handleChange}
                                fullWidth
                                InputLabelProps={{
                                    shrink: true,
                                }}
                            />
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