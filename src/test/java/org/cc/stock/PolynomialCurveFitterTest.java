package org.cc.stock;


import java.util.List;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class PolynomialCurveFitterTest {

    public static void main(String[] args) {

        WeightedObservedPoints obs = new WeightedObservedPoints();
        obs.add(2D,2.9D);
        obs.add(6D,3.0D);
        obs.add(8D,4.8D);
        obs.add(3D,1.8D);
        obs.add(2D,2.9D);

        //obs.add(0,5);
        //obs.add(0,5);
        //obs.add(0,5);
        //obs.add(0,5);
        //obs.add(0,5);

        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(4);
        List<WeightedObservedPoint> obs2List = obs.toList();

        //y =-0.008983686067019405*a^4 + 0.1248567019400353*b^3 + -0.23481040564373887*c^2 + -1.7142857142857142*d + 6.412698412698411
        final double[] coeff = fitter.fit(obs2List);
        for (int i = 0; i <coeff.length ; i++) {
            System.out.println(coeff[i]);
        }
    }
}