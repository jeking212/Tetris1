package com.tetris.saar.tetris;

/**
 * The type Line.
 */
//Class for straight line
public class Line extends Blocks{
    /**
     * Instantiates a new Line.
     *
     * @param i the
     * @param j the j
     */
    public Line(int i, int j){
        super(true,false,true,false,false,false,false,false,4,i,j);
    }

    /**
     * Instantiates a new Line.
     *
     * @param line the line
     * @param i    the
     * @param j    the j
     */
    public Line(Line line , int i, int j){
        super(line,i,j);
    }
    //Rotation
    @Override
    public void changeRot(){
        if(this.id==8 && this.getPlace()[1] ==23){
            this.setPlace(this.getPlace()[0],this.getPlace()[1]-1);
            changeRot();
        }else {
            if (getRotation() == 3) {
                this.rotation = 0;
            } else {
                this.rotation = getRotation() + 1;
            }
            //Setting all the new block shaped
            switch (rotation) {
                case 0:
                    setLeft(true);
                    setUp(false);
                    setRight(true);
                    setDown(false);
                    break;
                case 1:
                    setLeft(false);
                    setUp(true);
                    setRight(false);
                    setDown(true);
                    break;
                case 2:
                    setLeft(true);
                    setUp(false);
                    setRight(true);
                    setDown(false);
                    break;
                case 3:
                    setLeft(false);
                    setUp(true);
                    setRight(false);
                    setDown(true);
                    break;
            }
        }

    }
}


