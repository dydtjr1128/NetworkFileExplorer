import React from 'react';
import { observer } from 'mobx-react';
import { makeStyles } from '@material-ui/core/styles';
import CircularProgress from '@material-ui/core/CircularProgress';
import { Fade } from '@material-ui/core';
import useStores from '../util/useStore'

const overlayLoadingStyle = makeStyles(theme => ({

    loadingContent: {
        width: '100%',
        height: '100%',
        position: "absolute",
        background: "rgba(1, 1, 1, 0.4)",
        top: '0px',
        left: '0px',
        display: 'flex',
        textAlign: 'center',
        fontSize: '1.5em',
        zIndex: 800,
    },
    loadingText: {
        margin: 'auto',
        fontSize: '1.2em',
        color: '#FFF',
    }
}));

let index = 0;
const load = ['.', '..', '...','....','.....'];
const OverlayLoading = observer((props) => {
    const classes = overlayLoadingStyle();
    const { store } = useStores();    
    const [text, setText] = React.useState(load[0]);    
    React.useEffect(() => {
        setInterval(() => {
            setText(load[index++])            
            if(index > 5)
                index = 0
        }, 500);
    }, []);
    return (
        <div className="disable-select">
            <Fade in={store.progressVisible} timeout={80}>
                <div className={classes.loadingContent}>
                    <div className={classes.loadingText}><div><CircularProgress size={100} /></div>Loading{text}</div>
                </div>
            </Fade>
        </div >

    );
});

export default OverlayLoading;