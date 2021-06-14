import numbers
import numpy as np

def normalize(data, domain_min, domain_max):
    """
    :param data: the data to be normalized. Should be a numpy array or a number.
    :param domain_min: the minimum possible number in the domain of values of the data
    :param domain_max: the maximum possible number in the domain of values of the data
    :return: the normalized data, containing values in [0, 1]

    Note: This function might not work as expected or crash if data contains out of domain values or different values (e.g. NaN or None)
    """
    if (type(data) != np.ndarray) and (not isinstance(data, numbers.Number)):
        raise ValueError("data should be numpy ndarray or a number")
    if (not isinstance(domain_max, numbers.Number)) or (not isinstance(domain_min, numbers.Number)):
        raise ValueError("domain min and max should be numbers")

    return (data - domain_min)/(domain_max - domain_min)