import numpy as np
from scipy.spatial import distance



def get_weights(pdata, point, fdist, finvalid):
    """
    :param pdata: 2 dimensional matrix containing the data (just x and y axis, no time/feat)
    :param point: point of interest, the point that we want to aproximate as (x, y) touple
    :param fdist: function to calculate the distance by
    :param finvalid: function that checks if a value is invalid
    :return: the 2-dimensional matrix containing the weights for the neighbours
    """
    dist_sum = 0
    weights = np.zeros(pdata.shape)
    for rowNo in range(len(pdata)):
        for colNo in range(len(pdata[rowNo])):
            if not finvalid(pdata[rowNo][colNo]):
                dist = fdist((rowNo, colNo), (point[0], point[1]))
                weights[rowNo][colNo] = 1 / dist if dist != 0 else 0
                dist_sum += 1 / dist if dist != 0 else 0
    if dist_sum != 0:
        weights = weights / dist_sum
    return weights

def aprox_point(fdata, point, No_neighbours, fdist, finvalid):
    """
    :param fdata: 2 dimensional matrix containing the data
    :param point: 2-tuple as (x coordonate, y coordonate)
    :param No_neighbours:  number of neighbours in each direction to take
    :param fdist: function to calculate the distance by
    :param finvalid: function that checks if a value is invalid
    :return: the approximation of the point
    """

    x_start = point[0] - No_neighbours if point[0] >= No_neighbours else 0
    x_end = point[0] + No_neighbours + 1

    y_start = point[1] - No_neighbours if point[1] >= No_neighbours else 0
    y_end = point[1] + No_neighbours + 1

    pdata = fdata[x_start: x_end, y_start: y_end]
    weights = get_weights(pdata, (point[0] - x_start, point[1] - y_start), fdist, finvalid)
    if np.sum(weights) != 0:
        return np.average(pdata, weights=weights)
    else:
        return aprox_point(fdata, point, No_neighbours + 1, fdist, finvalid)

def preprocess_v(data, finvalid, No_neighbours = 6, fdist = distance.euclidean, fdiscretize = round):
    """
    Changes data so that all invalid points are replaced with their approximations.
    :param data: a 2 dimensional matrix containing the data  for one product at one time
    :param finvalid: function that checks if a value is invalid
    :param No_neighbours: number of neighbours in each direction to take
    :param fdist: function to calculate the distance by
    :param fdiscretize: function to compute a correct value for the product from the approximation (which is a real number)

    Note: This function works only if either data contains no elements other than numbers (e.g. Nan, None), or
          either if such elements are flagged as invalid by the finvalid function
    """
    data[np.isnan(data)] = 0
    invalid_points = list(zip(*np.where(finvalid(data))))

    for point in invalid_points:
        newp = aprox_point(data, point, No_neighbours, fdist, finvalid)
        data[point] = fdiscretize(newp)






