import React from 'react';
import { Grid, GridProps } from '@mui/material';

interface CustomGridProps extends GridProps {
  item?: boolean;
}

const CustomGrid: React.FC<CustomGridProps> = ({ children, ...props }) => {
  return <Grid {...props}>{children}</Grid>;
};

export default CustomGrid; 