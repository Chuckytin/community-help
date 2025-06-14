import React from 'react';
import { Grid, GridProps } from '@mui/material';

type CustomGridProps = GridProps & {
  children?: React.ReactNode;
};

const CustomGrid: React.FC<CustomGridProps> = ({ 
  children, 
  ...props 
}) => {
  return (
    <Grid {...props}>
      {children}
    </Grid>
  );
};

export default CustomGrid;