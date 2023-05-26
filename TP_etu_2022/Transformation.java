
import algebra.*;

/**
 * author: cdehais
 */
public class Transformation {

    Matrix worldToCamera;
    Matrix projection;
    Matrix calibration;

    public Transformation() {
        try {
            worldToCamera = new Matrix("W2C", 4, 4);
            projection = new Matrix("P", 3, 4);
            calibration = Matrix.createIdentity(3);
            calibration.setName("K");
        } catch (InstantiationException e) {
            /* should not reach */
        }
    }

    public void setLookAt(Vector3 cam, Vector3 lookAt, Vector3 up) {
        try {

            // compute rotation
            Vector3 e1c = new Vector3("e1c");
            Vector3 e2c = new Vector3("e2c");
            Vector3 e3c = new Vector3("e3c");

            // Normalisation de lookAt
            e3c = lookAt; 
            e3c.subtract(cam);
            if (lookAt.norm() != 0) {
                e3c.normalize();
            }

            // Produit vectoriel de up et e3c
            e1c = up.cross(e3c);
            if (e1c.norm() != 0) {
                e1c.normalize();
            }
            // Produit vectoriel de e3c et e1c
            e2c = e3c.cross(e1c);

            Matrix Nt = new Matrix("Nt", 3, 3);
            for (int j = 0; j < 3; j++) {
                Nt.set(0, j, e1c.get(j));
                Nt.set(1, j, e2c.get(j));
                Nt.set(2, j, e3c.get(j));
            }

            // worldToCamera.setSubMatrix (0, 0, Nt);
            for (int i = 0; i < 3; i++) {
                worldToCamera.set(i, 0, Nt.get(i, 0));
                worldToCamera.set(i, 1, Nt.get(i, 1));
                worldToCamera.set(i, 2, Nt.get(i, 2));
            }

            worldToCamera.set(3, 0, 0.0);
            worldToCamera.set(3, 1, 0.0);
            worldToCamera.set(3, 2, 0.0);
            worldToCamera.set(3, 3, 1.0);

            // compute translation
            Vector3 t = new Vector3("t");
            t.set(0, -e1c.dot(cam));
            t.set(1, -e2c.dot(cam));
            t.set(2, -e3c.dot(cam));

            for (int i = 0; i < 3; i++) {
                worldToCamera.set(i, 3, t.get(i));
            }

        } catch (Exception e) {
            /* unreached */ }
        ;
    }

    public void setProjection() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    projection.set(i, j, 1.0);
                } else {
                    projection.set(i, j, 0.0);
                }
            }
        }
    }

    public void setCalibration(double focal, double width, double height) {
        calibration.set(0, 0, focal);
        calibration.set(1, 1, focal);
        calibration.set(0, 2, width / 2);
        calibration.set(1, 2, height / 2);
    }

    /**
     * Projects the given homogeneous, 4 dimensional point onto the screen.
     * The resulting Vector as its (x,y) coordinates in pixel, and its z coordinate
     * is the depth of the point in the camera coordinate system.
     */
    public Vector3 projectPoint(Vector p)
            throws SizeMismatchException, InstantiationException {
        Vector ps = new Vector(3);

        Vector p1 = worldToCamera.multiply(p);
        Vector p2 = projection.multiply(p1);
        ps = calibration.multiply(p2);

        ps.set(0, ps.get(0)/ps.get(2));
        ps.set(1, ps.get(1)/ps.get(2));

        return new Vector3(ps);
    }

    /**
     * Transform a vector from world to camera coordinates.
     */
    public Vector3 transformVector(Vector3 v)
            throws SizeMismatchException, InstantiationException {
        /* Doing nothing special here because there is no scaling */
        Matrix R = worldToCamera.getSubMatrix(0, 0, 3, 3);
        Vector tv = R.multiply(v);
        return new Vector3(tv);
    }

}
