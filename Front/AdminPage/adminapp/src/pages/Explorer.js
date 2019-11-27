import React from 'react';
import Browser from 'component/Browser';
import { Redirect } from "react-router-dom";

export default function Explorer(props) {
  return (     
    props.authenticated ? <Browser/> : <Redirect to='/'/>    
  );
}