import React from 'react';
import { Route, Redirect } from "react-router-dom";
import { getThemeProps } from '@material-ui/styles';


export default function PrivateRoute(props) {
  console.log(props)
  
  return (
    props.authenticated ? (
    <Route path={props.path} component={props.component} />
    ) :
    (
      <Redirect
          to='/'
          />
    )
  );
}
  /*  <Route
    {...rest}
    render={props =>
      authenticated ? (
        <Component {...rest} {...props} />
      ) : (
        <Redirect
          to={{
            pathname: '/',
            state: { from: props.location }
          }}
        />
      )
    }
  />
);*/
